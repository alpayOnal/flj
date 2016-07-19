from django.contrib.auth.models import User
from django.db import models
from django.contrib.gis.db import models as gis_models

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    gcm_id = models.CharField(max_length=200, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)


class JobPost(models.Model):
    STATE_INACTIVE = 0
    STATE_ACTIVE = 1
    STATES = [
        (0, "inactive"),
        (1, "active")
    ]

    user = models.ForeignKey(
        User, on_delete=models.CASCADE, related_name='job_posts')
    state = models.SmallIntegerField(choices=STATES, default=STATES[1][0])
    post_id = models.AutoField(primary_key=True)
    created_at = models.DateTimeField(auto_now_add=True)
    title = models.CharField(max_length=255, blank=False)
    description = models.TextField(blank=False)
    created_at = models.DateTimeField(auto_now_add=True)
    city = models.CharField(max_length=25, blank=False)
    country = models.CharField(max_length=25, blank=False)
    # longitude = models.FloatField(blank=None)
    # latitude = models.FloatField(blank=None)
    point = gis_models.PointField()

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