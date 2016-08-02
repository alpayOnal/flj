import random

from django.contrib.auth.models import User
from django.db import models


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
        return random.randint(100000, 10000000)

    def check_credential(self, credential):
        return str(self.credential) == str(credential)


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
    title = models.CharField(max_length=255, blank=False)
    description = models.TextField(blank=False)
    created_at = models.DateTimeField(auto_now_add=True)
    city = LowerCaseCharField(max_length=25, blank=False)
    country = LowerCaseCharField(max_length=25, blank=False)
    longitude = models.FloatField(blank=None)
    latitude = models.FloatField(blank=None)
    # point = gis_models.PointField()


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
