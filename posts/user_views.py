from django.contrib.auth.models import User
from posts.models import StarredJobs
from posts.permissions import IsSelf, IsOwner
from posts.serializers import UserProfileSerializer, UserSerializer, \
    StarredJobsSerializer
from rest_framework import generics
from rest_framework import permissions

# @TODO: switch to generics.CreateAPIView
from rest_framework.permissions import IsAuthenticated


class UserList(generics.ListCreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveUpdateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = (IsSelf,)


class StarredJobsList(generics.ListCreateAPIView):
    serializer_class = StarredJobsSerializer
    permission_classes = (IsAuthenticated, )

    def get_queryset(self):
        user = self.request.user
        return StarredJobs.objects.filter(user=user.id)


class StarredJobDetail(generics.DestroyAPIView):
    queryset = StarredJobs.objects.all()
    serializer_class = StarredJobsSerializer
    permission_classes = (IsOwner,)
    lookup_field = "job"
