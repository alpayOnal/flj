import random

from django.contrib.auth.models import User
from django.core.exceptions import ObjectDoesNotExist
from django.db import models
import hashlib


class LowerCaseCharField(models.CharField):
    def get_prep_value(self, value):
        value = super(LowerCaseCharField, self).get_prep_value(value)
        if value is not None:
            value = value.lower()
        return value


class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    gcm_id = models.CharField(max_length=200, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    picture = models.CharField(max_length=255, blank=True)
    credential = models.CharField(max_length=100, blank=True)

    def generate_credential(self):
        rand_chunk = random.randint(100000, 10000000)
        credential = hashlib.md5()
        credential.update("{}{}{}".format(
            self.id, self.created_at, rand_chunk).encode("UTF-8"))
        return credential.hexdigest()

    def check_credential(self, credential):
        return str(self.credential) == str(credential)

    @classmethod
    def getOrCreateUser(cls, username, email, first_name, last_name, picture):
        """
        find user associated with username and updates its data or create a user

        :param username:
        :param email:
        :param first_name:
        :param last_name:
        :param picture:
        :return: User
        """
        try:
            user = User.objects.get(username=username)
            profile = user.userprofile
            credential = profile.credential
        except ObjectDoesNotExist:
            user = User()
            user.email = email
            user.username = username
            profile = UserProfile()
            credential = profile.generate_credential()

        user.first_name = first_name
        user.last_name = last_name
        user.save()

        profile.credential = credential
        profile.picture = picture
        profile.user = user
        profile.save()
        return user


class JobPost(models.Model):
    # @TODO: email must be lowercase too. check it out.
    STATE_INACTIVE = 0
    STATE_ACTIVE = 1
    STATES = [
        (0, "inactive"),
        (1, "active")
    ]
    job_id = models.AutoField(primary_key=True)
    user = models.ForeignKey(
        User, on_delete=models.CASCADE, related_name='job_posts')
    employer = models.CharField(max_length=50, blank=False)
    state = models.SmallIntegerField(choices=STATES, default=STATES[1][0])
    created_at = models.DateTimeField(auto_now_add=True)

    title = models.CharField(max_length=255, blank=False)
    description = models.TextField(blank=False)

    city = LowerCaseCharField(max_length=25, blank=False)
    country = LowerCaseCharField(max_length=25, blank=False)
    longitude = models.FloatField(blank=None)
    latitude = models.FloatField(blank=None)

    # if post is crawled
    source_url = models.CharField(max_length=10000, blank=True)

    # if eligible for job applications in app
    # if not, probably end users will be redirected to the source url
    applicable = models.BooleanField(default=True, blank=True)

    # how many times this record is viewed
    view_counter = models.IntegerField(default=0, blank=True)

    # point = gis_models.PointField()

    def countView(self, addition=1):
        self.view_counter += addition


class StarredJob(models.Model):
    class Meta:
        unique_together = ("job", "user",)

    created_at = models.DateTimeField(auto_now_add=True)
    job = models.ForeignKey(
        to="JobPost",
        on_delete=models.CASCADE,
        related_name="jobpost_id")
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='starred_job')


class Alarm(models.Model):
    updated_at = models.DateTimeField(auto_now=True)
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name="alarm"
    )
    keyword = LowerCaseCharField(max_length=50, blank=False)
    country = LowerCaseCharField(max_length=25, blank=False)
    city = models.CharField(max_length=25, blank=False)
