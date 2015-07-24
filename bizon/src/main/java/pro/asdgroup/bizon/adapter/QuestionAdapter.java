package pro.asdgroup.bizon.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.model.DayQuestion;

/**
 * Created by Voronov Viacheslav on 06.07.2015.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    List<DayQuestion> mQuestions;

    public QuestionAdapter(){
        mQuestions = new ArrayList<>();
    }

    public void addItems(List<DayQuestion> questions){
        mQuestions.addAll(questions);
        notifyDataSetChanged();
    }

    public void addItem(DayQuestion question){
        mQuestions.add(question);
        notifyItemInserted(mQuestions.size() - 1);
    }

    public DayQuestion getItem(int position){
        return mQuestions.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_question, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        DayQuestion question = mQuestions.get(i);
        //h.dateText.setText(DateFormat.getDateInstance().format(question.getDate()));
        h.titleText.setText(question.getText());
        h.currentQuestionIndicator.setVisibility(question.getIsDayQuestion() == 1? View.VISIBLE: View.GONE); //// FIXME: 06.07.2015 hardcoded
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //@InjectView(R.id.dateText) TextView dateText;
        @InjectView(R.id.titleText) TextView titleText;
        @InjectView(R.id.currentQuestionIndicator) TextView currentQuestionIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
