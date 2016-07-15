from django.conf.urls import url
from posts import user_views, job_post_view
from rest_framework.urlpatterns import format_suffix_patterns

urlpatterns = [
    url(r'^users/$', user_views.UserList.as_view()),
    url(r'^users/(?P<pk>[0-9]+)/$', user_views.UserDetail.as_view()),
    url(r'^posts/$', job_post_view.JobPosts.as_view()),
    url(r'^posts/(?P<pk>[0-9]+)/$', job_post_view.JobPostDetail.as_view()),
    url(r'^starredjobs/$', user_views.StarredJobsList.as_view()),
    url(r'^starredjobs/(?P<job>[0-9]+)/$', user_views.StarredJobDetail.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
