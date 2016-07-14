from rest_framework import serializers
from django.contrib.auth.models import User
from .models import JobPost, Location, UserProfile


class StarredJobs(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ("stared_jobs", )

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ("created_at", "gcm_id")


class UserSerializer(serializers.ModelSerializer):

    userprofile = UserProfileSerializer()
    password = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ("id", "username", "email", "password", "userprofile")

    def create(self, validated_data):
        profile_data = validated_data.pop("userprofile")
        password = validated_data["password"]
        user = User.objects.create(**validated_data)
        user.set_password(password)
        user.save()
        UserProfile.objects.create(user=user, **profile_data)
        return user

    def update(self, instance, validated_data):
        profile_data = validated_data.pop("userprofile")
        instance.email = validated_data["email"]
        instance.set_password(validated_data["password"])
        instance.save()

        ups = UserProfileSerializer(
            instance=instance.userprofile, data=profile_data)
        ups.is_valid(raise_exception=True)
        ups.save(**profile_data)

        return instance


class JobPostSerializer(serializers.ModelSerializer):

    class Meta:
        model = JobPost
        fields = ("title", "description", "user")
        read_only_fields = ("user", )

    def create(self, validated_data):
        user_profile = self.context['request'].user.userprofile
        validated_data["user"] = user_profile
        post = JobPost.objects.create(**validated_data)
        return post
