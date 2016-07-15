from django.db.models import Q
from posts.helpers import JSONResponse
from posts.models import JobPost
from posts.permissions import IsOwnerOrReadOnly
from posts.serializers import JobPostSerializer
from rest_framework import generics
from rest_framework import permissions


class JobPosts(generics.ListCreateAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer
    # permission_classes = (IsOwnerOrReadOnly,)
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def get_queryset(self):
        # location, keyword
        # if self.request.get("")
        keyword = self.request.query_params.get("keyword")
        location = self.request.query_params.get("location")
        criteria = []
        if keyword:
            criteria.append(
                Q(title__icontains=keyword) | Q(description__icontains=keyword))
        if location:
            pass
            #criteria.append()

        queryset = JobPost.objects.filter(*criteria).order_by("-created_at")
        # return JobPost.objects.all()
        return queryset


class JobPostDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer

