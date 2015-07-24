package pro.asdgroup.bizon.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.EventInvitationActivity;
import pro.asdgroup.bizon.activity.EventNewsActivity;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.BasePagerFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.ActionResult;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Publisher;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.view.FlowLayout;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class EventFragment extends BasePagerFragment implements View.OnClickListener {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";

    Event mEvent;
    String fragmentTitle;

    @InjectView(R.id.publish_date_text) TextView mPublishDateText;
    @InjectView(R.id.publisher_text) TextView mPublisherText;
    @InjectView(R.id.body_text) TextView mBodyText;
    @InjectView(R.id.description_text) TextView mDescriptionText;
    @InjectView(R.id.tags_layout) FlowLayout mHashtagLayout;
    @InjectView(R.id.event_photo) ImageView mEventImage;
    @InjectView(R.id.participation_status_image) ImageView mPartStatus;
    @InjectView(R.id.invite_btn) Button mInviteButton;
    @InjectView(R.id.join_btn) Button mJoinButton;

    public static EventFragment newInstance(Event event) {

        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, event.getId());
        args.putString(ARG_TITLE, event.getName());
        fragment.setArguments(args);
        return fragment;
    }

    public EventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoeuil.ttf");
        mDescriptionText.setTypeface(font);

        font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
        mPublisherText.setTypeface(font);
        mPublishDateText.setTypeface(font);

        Bundle args = getArguments();
        fragmentTitle = args.getString(ARG_TITLE);
        mEvent = new Event();
        mEvent.setId(args.getInt(ARG_ID));

        setHasOptionsMenu(true);

        initLoader();
    }

    private void initLoader() {
        onLoadBegins();

        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        if (User.currentUser().getProfile() == null) {
            service.getEvent(mEvent.getId(), new EventCallback());
        } else {
            service.getEvent(mEvent.getId(), User.currentUser().getUserId(), new EventCallback());
        }
    }

    class EventCallback extends HttpHelper.RestCallback<Event> {
        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }

        @Override
        public void success(Event event, Response response) {
            if (getView() == null){
                return;
            }

            mEvent = event;

            updateParticipationStatus();
            onLoadSuccess();
        }
    }

    public void onLoadSuccess() {

        if (mEvent.getPictureUrl() == null){
            onLoadFinished();
            return;
        }

        HttpHelper.getInstance(getActivity()).getImageLoader().get(mEvent.getPictureUrl(), new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onLoadFinished();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (mEventImage != null) {
                    mEventImage.setImageBitmap(imageContainer.getBitmap());
                }
                onLoadFinished();
            }
        });
    }

    protected void onLoadFinished(){
        updateText();
        super.onLoadFinished();
    }

    private void updateText() {

        if (getView() == null){
            return;
        }

        getActivity().invalidateOptionsMenu();

        String eventDateTime = mEvent.getPublishedDateText(true) + " " + getString(R.string.at_o_clock)
                + " " + mEvent.getPublishedTime(true);

        mPublishDateText.setText(eventDateTime);

        Publisher publisher = mEvent.getPublisher();
        String publisherText = publisher.getFirstName() + " " + publisher.getLastName();

        mPublisherText.setText(publisherText);
        mBodyText.setText(Html.fromHtml(mEvent.getText()));
        mDescriptionText.setText(mEvent.getSmallDescription());

        mHashtagLayout.removeAllViews();

        int padding = (int) getActivity().getResources().getDimension(R.dimen.hashtag_padding);

        if (mEvent.getHashTags() == null){
            return;
        }

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GretaTextPro-Bold.otf");

        for (HashTag hashTag : mEvent.getHashTags()) {
            TextView hashTagView = new TextView(getActivity());
            hashTagView.setText(hashTag.toString());
            hashTagView.setTag(hashTag);
            hashTagView.setTextColor(getResources().getColor(R.color.hash_tag));
            hashTagView.setOnClickListener(this);
            hashTagView.setPadding(padding, padding, padding, padding);
            hashTagView.setTypeface(font);

            mHashtagLayout.addView(hashTagView);
        }
    }

    private void updateParticipationStatus(){
        View view = getView();
        if (view == null){
            return;
        }

        if (mEvent.getIsParticipant()){
            mInviteButton.setVisibility(View.VISIBLE);
            mJoinButton.setVisibility(View.GONE);
            mPartStatus.setVisibility(View.VISIBLE);
        } else {
            mInviteButton.setVisibility(View.GONE);
            mJoinButton.setVisibility(View.VISIBLE);
            mPartStatus.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.publisher_text)
    void onPublisherClick() {
        Fragment fragment = CommunityUserFragment.newInstance(mEvent.getPublisher());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();

        fragment.setTargetFragment(getParentFragment(), 10);
    }

    @OnClick(R.id.join_btn)
    void onJoinClick(){
        if (User.currentUser().getProfile() == null){
            Toast.makeText(getActivity(), R.string.ep_no_auth_user_join, Toast.LENGTH_SHORT).show();
            return;
        }

        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.acceptEvent(User.currentUser().getUserId(),
                mEvent.getId(),
                new HttpHelper.RestCallback<ActionResult>() {
                    @Override
                    public void failure(Status restError) {
                        onLoadFinished();
                    }

                    @Override
                    public void success(ActionResult result, Response response) {
                        mEvent.setIsMember(result.getSuccess());
                        updateParticipationStatus();
                        getActivity().invalidateOptionsMenu();
                        onLoadSuccess();
                    }
                });
    }

    @OnClick(R.id.participants_btn)
    void onParticipantsClick(){
        Fragment fragment = CommunityListFragment.newInstance(mEvent.getId());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();

        fragment.setTargetFragment(getParentFragment(), 10);
    }

    @OnClick(R.id.invite_btn)
    void onInviteClick(){
        Intent intent = new Intent(getActivity(), EventInvitationActivity.class);
        intent.putExtra(EventInvitationActivity.ARG_EVENT_ID, mEvent.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        HashTag hashTag = (HashTag) v.getTag();

        EventListFragment fragment = EventListFragment.newInstance(hashTag);
        BaseFragment parentFragment = (BaseFragment) getParentFragment();
        if (parentFragment == null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .commit();
        } else {
            parentFragment.addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mEvent != null && mEvent.getNewsCount() > 0) {
            inflater.inflate(R.menu.event, menu);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_news){
            onNewsMenuClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onNewsMenuClick(){
        Intent intent = new Intent(getActivity(), EventNewsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EventNewsActivity.ARG_EVENT, mEvent);
        intent.putExtras(bundle);

        startActivity(intent);

        /*Fragment fragment = EventNewsFragment.newInstance(mEvent);
        BaseFragment parentFragment = (BaseFragment) getParentFragment();
        parentFragment.addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
