from rest_framework import serializers
from django.contrib.auth.models import User
from .models import JobPost, UserProfile, StarredJob, Alarm


class StarredJobsSerializer(serializers.ModelSerializer):
    class Meta:
        model = StarredJob
        fields = ("job", )

    def create(self, validated_data):
        user = self.context['request'].user
        validated_data["user_id"] = user.id
        return super(StarredJobsSerializer, self).create(validated_data)


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
    state = serializers.SerializerMethodField()

    def get_state(self, obj):
        """
        instade of displaying code of choice, display its label.
        :param obj:
        :return:
        """
        return obj.get_state_display()

    class Meta:
        model = JobPost
        fields = (
            "created_at", "state", "title", "description", "user",
            "city", "country", "latitude", "longitude")
        read_only_fields = ("user", "created_at", )

    def create(self, validated_data):
        user = self.context["request"].user
        validated_data["user"] = user
        post = JobPost.objects.create(**validated_data)
        return post


class AlarmSerializer(serializers.ModelSerializer):

    class Meta:
        model = Alarm
        read_only_fields = ("user", "updated_at")

    def create(self, validated_data):
        user = self.context["request"].user
        validated_data["user"] = user
        alarm = Alarm.objects.create(**validated_data)
        return alarm
