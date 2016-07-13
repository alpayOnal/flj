from rest_framework import serializers
from django.contrib.auth.models import User
from .models import JobPost, Location, UserProfile


class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ("created_at", )


class UserSerializer(serializers.ModelSerializer):

    userprofile = UserProfileSerializer()

    class Meta:
        model = User
        fields = ("username", "email", "userprofile")

    def create(self, validated_data):
        print(validated_data)
        profile_data = validated_data.pop("userprofile")
        user = User.objects.create(**validated_data)
        UserProfile.objects.create(user=user, **profile_data)
        return user


class JobPostSerializer(serializers.ModelSerializer):
    class Meta:
        model = JobPost