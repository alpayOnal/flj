from django.contrib.auth.models import User
from rest_framework import viewsets
from rest_framework.decorators import list_route
from rest_framework.exceptions import AuthenticationFailed
from rest_framework.response import Response

from posts.models import StarredJob, Alarm
from posts.permissions import IsSelf, IsOwner
from posts.serializers import UserProfileSerializer, UserSerializer, \
    StarredJobsSerializer, AlarmSerializer
from rest_framework import generics
from rest_framework import permissions

# @TODO: switch to generics.CreateAPIView
from rest_framework.permissions import IsAuthenticated


class UserAuthentication(viewsets.ModelViewSet):
    """
    this is used to verify whether authentication is valid or not.
    some clients want to know if authentication is valid, so they
    request a url looks like `/users/me`.
    """
    @list_route()
    def me(self, request, *args, **kwargs):
        if request.user.is_authenticated():
            return Response(UserSerializer(request.user).data)
        raise AuthenticationFailed()


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
        return Alarm.objects.filter(user=user.id).order_by("-updated_at")


class AlarmDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = Alarm.objects.all()
    serializer_class = AlarmSerializer
    permission_classes = (IsOwner,)
