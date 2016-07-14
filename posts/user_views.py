from django.contrib.auth.models import User
from posts.permissions import IsSelf
from posts.serializers import UserProfileSerializer, UserSerializer
from rest_framework import generics


# @TODO: switch to generics.CreateAPIView
class UserList(generics.ListCreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveUpdateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = (IsSelf,)


