package pro.asdgroup.bizon.data;

import java.util.List;

import pro.asdgroup.bizon.model.ActionResult;
import pro.asdgroup.bizon.model.Article;
import pro.asdgroup.bizon.model.Company;
import pro.asdgroup.bizon.model.CompanySearch;
import pro.asdgroup.bizon.model.DayQuestion;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.EventHtml;
import pro.asdgroup.bizon.model.EventNew;
import pro.asdgroup.bizon.model.FeedComment;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.InvitationContact;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.UserAvatar;
import pro.asdgroup.bizon.model.dto.PostDTO;
import pro.asdgroup.bizon.model.dto.ProfileDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Tieru on 15.05.2015.
 */
public interface RestHelper {

    @GET("/register")
    void register(@Query("firstname") String firstName,
                  @Query("middlename") String middleName,
                  @Query("lastname") String lastName,
                  @Query("city_name") String cityName,
                  @Query("company_name") String companyName,
                  @Query("password") String password,
                  @Query("email") String email,
                  @Query("device_type") String deviceType,
                  @Query("push_token") String pushToken,
                  Callback<Profile> cb);

    @GET("/register")
    void register(@Query("firstname") String firstName,
                  @Query("middlename") String middleName,
                  @Query("lastname") String lastName,
                  @Query("city_name") String cityName,
                  @Query("company_name") String companyName,
                  @Query("password") String password,
                  @Query("email") String email,
                  @Query("login_method") String loginMethod,
                  @Query("social_id") String socialId,
                  @Query("social_token") String socialToken,
                  @Query("device_type") String deviceType,
                  @Query("push_token") String pushToken,
                  Callback<Profile> cb);

    @GET("/checkSocialUser")
    void checkUser(@Query("login_method") String loginMethod,
                   @Query("social_id") String socialId,
                   @Query("social_token") String socialToken,
                   @Query("push_token") String pushToken,
                   @Query("device_type") String deviceType,
                   Callback<ProfileDTO> cb);

    @POST("/uploadavatar")
    void uploadAvatar(@Body UserAvatar avatar,
                      Callback<Response> cb);

    @GET("/login")
    void authorize(@Query("email") String email,
                   @Query("password") String password,
                   @Query("device_type") String deviceType,
                   @Query("push_token") String pushToken,
                   Callback<Profile> cb);

    @POST("/updateprofile")
    void updateProfile(@Body Profile profile,
                       Callback<Profile> cb);

    @GET("/restorepassword")
    void restorePassword(@Query("email") String email,
                         Callback<Status> cb);

    @GET("/user")
    void getUser(@Query("user_id") String userId,
                 Callback<Profile> cb);

    @GET("/createcompany")
    void createCompany(@Query("user_id") String userId,
                       @Query("name") String name,
                       @Query("about") String about,
                       @Query("business") String business,
                       @Query("site") String site,
                       Callback<Company> cb);

    @GET("/editcompany")
    void updateCompany(@Query("user_id") String userId,
                       @Query("company_id") int companyId,
                       @Query("name") String name,
                       @Query("about") String about,
                       @Query("business") String business,
                       @Query("site") String site,
                       Callback<Company> cb);

    @GET("/companies?page=1&count=10")
    CompanySearch searchCompanies(@Query("text")String searchString);

    @GET("/articles")
    void getArticles(@Query("page") int page,
                     @Query("count") int count,
                     Callback<List<Article>> cb);

    @GET("/articles")
    void getArticles(@Query("author_id") String authorId,
                     @Query("page") int page,
                     @Query("count") int count,
                     Callback<List<Article>> cb);

    @GET("/articles")
    void getArticles(@Query("page") int page,
                     @Query("count") int count,
                     @Query("hashtags[]") Integer[] hashTags,
                     Callback<List<Article>> cb);

    @GET("/article")
    void getArticle(@Query("article_id") int articleId,
                     Callback<Article> cb);

    @GET("/users")
    void getUsers(@Query("page") int page,
                  @Query("count") int count,
                  Callback<List<Profile>> cb);

    @GET("/users")
    void getUsers(@Query("search_str") String searchString,
                  @Query("page") int page,
                  @Query("count") int count,
                  Callback<List<Profile>> cb);

    @GET("/users")
    void getEventParticipants(@Query("search_str") String searchString,
                  @Query("page") int page,
                  @Query("count") int count,
                  @Query("event_id") int eventId,
                  Callback<List<Profile>> cb);

    @GET("/users?experts=1")
    void getExperts(@Query("search_str") String searchString,
                    @Query("page") int page,
                    @Query("count") int count,
                    Callback<List<Profile>> cb);

    @GET("/events")
    void getEvents(@Query("page") int page,
                   @Query("count") int count,
                   Callback<List<Event>> cb);

    @GET("/events")
    void getEvents(@Query("page") int page,
                   @Query("count") int count,
                   @Query("hashtags[]") int hashtag,
                   Callback<List<Event>> cb);

    @GET("/events")
    void getEvents(@Query("page") int page,
                   @Query("count") int count,
                   @Query("is_member") int status,
                   @Query("user_id") String userId,
                   Callback<List<Event>> cb);

    @GET("/events")
    void getEvents(@Query("page") int page,
                   @Query("count") int count,
                   @Query("is_member") int status,
                   @Query("user_id") String userId,
                   @Query("hashtags[]") int hashtag,
                   Callback<List<Event>> cb);

    @GET("/event")
    void getEvent(@Query("event_id") int id,
                   Callback<Event> cb);

    @GET("/event")
    void getEvent(@Query("event_id") int id,
                  @Query("user_id") String userId,
                  Callback<Event> cb);


    @GET("/eventnews")
    void getEventNews(@Query("event_id") int eventId,
                      @Query("page") int page,
                      @Query("count") int count,
                      Callback<List<EventNew>> cb);

    @GET("/addeventnews")
    void addEventNews(@Query("user_id") String userId,
                      @Query("event_id") int eventId,
                      @Query("text") String message,
                      Callback<Status> cb);

    @GET("/removeeventnews")
    void removeEventNews(@Query("user_id") String userId,
                      @Query("eventnews_id") String eventNewId,
                      Callback<Status> cb);

    @GET("/hashtags")
    void getHashtags(Callback<List<HashTag>> cb);

    @GET("/sendpushtoken?device_type=ANDROID")
    Response sendPushToken(@Query("user_id") String userId,
                      @Query("push_token") String pushToken);

    @GET("/acceptevent")
    void acceptEvent(@Query("user_id") String userId,
                     @Query("event_id") int eventId,
                     Callback<ActionResult> cb);


    @GET("/users")
    void getUserContacts(@Query("page") int page,
                  @Query("count") int count,
                  Callback<List<InvitationContact>> cb);

    @GET("/eventinvite")
    void eventInvite(@Query("user_id") String userId,
                     @Query("event_id") int eventId,
                     @Query("invited_ids[]") String[] ids,
                     Callback<ActionResult> cb);

    @GET("/eventinvitetext?device_type=ANDROID")
    void getEventInviteText(@Query("event_id") int eventId,
                            Callback<EventHtml> cb);


    @GET("/posts")
    void getFeed(@Query("page") int page,
                 @Query("count") int count,
                 @Query("cities_names[]") String[] citiesNames,
                 Callback<FeedEntry> cb);

    @GET("/posts")
    void getFeed(@Query("page") int page,
                 @Query("count") int count,
                 Callback<List<FeedEntry>> cb);

    @POST("/createpost")
    void createPost(@Body FeedEntry feedEntry,
                    Callback<PostDTO> cb);

    @POST("/editpost")
    void editPost(@Body FeedEntry feedEntry,
                    Callback<PostDTO> cb);

    @GET("/removepost")
    void removePost(@Query("post_id") String postId,
                    @Query("user_id") String userId,
                    Callback<ActionResult> cb);

    @GET("/post")
    void getPost(@Query("post_id") String postId,
                Callback<FeedEntry> cb);

    @GET("/eventcomments")
    void getEventComments(@Query("event_id") int eventId,
                          @Query("page") int page,
                          @Query("count") int count,
                          Callback<List<FeedComment>> cb);

    @GET("/commentpost")
    void commentPost(@Query("post_id") String postId,
                     @Query("text") String text,
                     @Query("user_id") String userId,
                     Callback<FeedComment> cb);

    @GET("/postcomments")
    void getPostComments(@Query("post_id") String postId,
                     @Query("count") int count,
                     @Query("showed_count") int displayedCount,
                     Callback<List<FeedComment>> cb);

    @GET("/editpostcomment")
    void editPostComment(@Query("post_id") String postId,
                     @Query("text") String text,
                     @Query("user_id") String userId,
                     @Query("comment_id") String commentId,
                     Callback<Response> cb);

    @GET("/removepostcomment")
    void removePostComment(@Query("post_id") String postId,
                     @Query("user_id") String userId,
                     @Query("comment_id") String commentId,
                     Callback<Response> cb);

    @GET("/WhoLikePost")
    void whoLikePost(Callback<List<String>> cb);

    @GET("/likepost")
    void likePost(@Query("post_id") String postId,
                  @Query("user_id") String userId,
                  Callback<ActionResult> cb);

    @GET("/unlikepost")
    void unlikePost(@Query("post_id") String postId,
                  @Query("user_id") String userId,
                  Callback<ActionResult> cb);

    @GET("/dayquestions")
    void getDailyQuestions(@Query("user_id") String userId,
                           Callback<List<DayQuestion>> cb);


    @GET("/dayquestion")
    void getDailyQuestion(@Query("user_id") String userId,
                           @Query("showed[]") String id[],
                           Callback<DayQuestion> cb);

    @GET("/dayquestioninfo")
    void getDailyQuestionInfo(@Query("question_id") String id,
                              Callback<DayQuestion> cb);

    @GET("/NotShow")
    void hideQuestion(@Query("user_id") String userId,
                      @Query("question_id") String questionId,
                      Callback<Response> cb);

}
