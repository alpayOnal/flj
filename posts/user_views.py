from django.contrib.auth.models import User
from posts.models import StarredJob, Alarm
from posts.permissions import IsSelf, IsOwner
from posts.serializers import UserProfileSerializer, UserSerializer, \
    StarredJobsSerializer, AlarmSerializer
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
        return StarredJob.objects.filter(user=user.id)


class StarredJobDetail(generics.DestroyAPIView):
    queryset = StarredJob.objects.all()
    serializer_class = StarredJobsSerializer
    permission_classes = (IsOwner,)
    lookup_field = "job"


class AlarmList(generics.ListCreateAPIView):
    serializer_class = AlarmSerializer
    permission_classes = (IsAuthenticated, )

    def get_queryset(self):
        user = self.request.user
        return Alarm.objects.filter(user=user.id)


class AlarmDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = Alarm.objects.all()
    serializer_class = AlarmSerializer
    permission_classes = (IsOwner,)
