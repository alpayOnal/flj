from posts.helpers import JSONResponse
from posts.models import JobPost
from posts.serializers import JobPostSerializer
from rest_framework import generics
from rest_framework import permissions


class JobPosts(generics.ListCreateAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)


class JobPostDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer

