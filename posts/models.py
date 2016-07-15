from django.contrib.auth.models import User
from django.contrib.gis.db.models import PointField
from django.db import models


class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    gcm_id = models.CharField(max_length=200, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)


class JobPost(models.Model):
    post_id = models.AutoField(primary_key=True)
    user = models.ForeignKey(
        User, on_delete=models.CASCADE, related_name='job_posts')
    # @TODO: imeplement later: status = models.choice
    title = models.CharField(max_length=255, blank=False)
    description = models.TextField(blank=False)
    created_at = models.DateTimeField(auto_now_add=True)
    city = models.CharField(max_length=25, blank=False)
    country = models.CharField(max_length=25, blank=False)
    longitude = models.FloatField(blank=None)
    latitude = models.FloatField(blank=None)


class StarredJobs(models.Model):
    class Meta:
        unique_together = ("job", "user",)

    job = models.ForeignKey(
        to="JobPost",
        on_delete=models.CASCADE,
        related_name="job_id")
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='user_id')