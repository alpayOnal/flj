from django.conf.urls import url
from rest_framework.routers import DefaultRouter

from posts import social_login_view
from posts import user_views, job_post_view
from rest_framework.urlpatterns import format_suffix_patterns




urlpatterns = [
    url(r'^users/$', user_views.UserList.as_view()),
    url(r'^users/me/$', user_views.UserAuthentication.as_view({"get": "me"}), name="getUser"),
    url(r'^users/(?P<pk>[0-9]+)/$', user_views.UserDetail.as_view(), name="getUser"),
    # url(r'^posts/$', job_post_view.JobPosts.as_view()),
    # url(r'^posts/(?P<pk>[0-9]+)/$', job_post_view.JobPostDetail.as_view()),
    url(r'^starredjobs/$', user_views.StarredJobsList.as_view()),
    url(r'^starredjobs/(?P<job>[0-9]+)/$', user_views.StarredJobDetail.as_view()),
    url(r'^alarms/$', user_views.AlarmList.as_view()),
    url(r'^alarms/(?P<pk>[0-9]+)/$', user_views.AlarmDetail.as_view()),
    url(r'^users/verifyGoogleSignin/$', social_login_view.verifyGoogleSignin),
    url(r'^users/verifyFacebookSignin/$', social_login_view.verifyFacebookSignin),
]

urlpatterns = format_suffix_patterns(urlpatterns)

router = DefaultRouter()
router.register(r'posts', job_post_view.JobPosts)
urlpatterns.extend(router.urls)

