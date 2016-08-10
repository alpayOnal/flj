import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'flj.settings'

from django import setup
setup()

from django.contrib.auth.models import User
from posts.models import JobPost
from posts.models import UserProfile

def truncate():
    User.objects.all().delete()
    User.objects.all().delete()
    JobPost.objects.all().delete()
truncate()

def create_user(email, password):
    u = User()
    up = UserProfile()
    u.username = email
    u.email = email
    u.set_password(password)
    u.save()
    up.user = u
    up.save()
    return up


muatik = create_user("muatik@gmail.com", '123456')
alpay = create_user("alpay@gmail.com", '123456')


JobPost(
    title="Software Engineer, PHP",
    description="You will be a part of an intense and autonomous team that works on the most challenging projects to create games that amaze millions of people all around the world. Within the agile environment, you will work alongside software engineers, artists, game designers and product managers in a thriving, fun and fast-paced atmosphere. https://www.linkedin.com/jobs/view/188363340?refId=bb7574d5-73c5-45b1-82c3-212685dcccd7&recommendedFlavor=IN_NETWORK&trk=jobshomev2_jymbii",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Peak games"
    ).save()

JobPost(
    title="Software Engineer",
    description="Automated white-box tests are written by developers to test code at a very low level of granularity. Developers use unit tests to drive the design of their stories, as well as to verify that their code works as designed and to assess unanticipated side effects. Developers are also responsible for writing integration level tests, which test the system at a higher level, with focus on the interaction amongst various subsystems. https://www.linkedin.com/jobs2/view/161222363?refId=b20de8b5-6197-4a1d-8fa0-a059f227d4ee&recommendedFlavor=IN_NETWORK&trk=job_view_browse_map",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Peak games"
    ).save()

JobPost(
    title="Java Developer 1",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 2",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 3",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 4",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="istanbul",
    country="turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 5",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="Berlin",
    country="Germany",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 6",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="Berlin",
    country="Germany",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Java Developer 7",
    description="Consultation in process and methods to optimize the processes in development and production.",
    city="Berlin",
    country="Germany",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Mercedes Benz"
    ).save()
JobPost(
    title="Python Developer 1",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="London",
    user=muatik.user,
    longitude=1, latitude=1,
    country="United Kingdom",
    employer="Bath"
    ).save()
JobPost(
    title="Python Developer 2",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="London",
    country="United Kingdom",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Bath"
    ).save()
JobPost(
    title="Python Developer 3",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="London",
    country="United Kingdom",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Bath"
    ).save()
JobPost(
    title="Python Developer 4",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="London",
    country="United Kingdom",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Bath"
    ).save()
JobPost(
    title="Python Developer 5",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="Istanbul",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Markafoni"
    ).save()
JobPost(
    title="Python Developer 6",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="Istanbul",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Markafoni"
    ).save()
JobPost(
    title="Python Developer 7",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="Istanbul",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()
JobPost(
    title="Python Developer 8",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="Kocaeli",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()
JobPost(
    title="Python Developer 9",
    description="A fantastic Python Developer / Python Engineer opportunity has arisen within a truly innovative tech company based in central Bath. "
                "Having enjoyed sustained growth over the last few years my client is now looking to strengthen their engineering team with an experience Python Developer / Python Engineer. "
                "Reporting directly into the CTO the Python Developer / Python Engineer is responsible for the development, testing and support of the software. The Python Developer / Python Engineer will maintain and develop the core application code, as well as designing and developing plugins extending the functionality. ",
    city="Kocaeli",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()
JobPost(
    title="Python Developer Django MongoDB - Bioinformatics",
    description="The 100,000 genomes project is a challenging and fast moving project with the aim to carry out whole genome sequencing on 100,000 participants. The client works with key partners to collect, transport, store, quality check and sequence the samples from participants. The aim for this project is to create a new genomic medicine service for the NHS - transforming the way people are cared for. Patients may be offered a diagnosis where there wasn't one before. In time, there is the potential of new and more effective treatments. "
                "This role is for an experienced Python developer with exposure to bioinformatician approaches and with web service expertise. As part of the team the post holder: "
                "*Produces high quality code *Liaises with internal and external parties to determine platform requirements "
                "*Performs quality assurance of the analysis and annotation made by third parties"
                "Skills, knowledge and experience:"
                "*First level degree in a quantitative discipline such as computer science, physics or mathematics. It is desirable to have some experience working in bioinformatics."
                "*Proven track record of developing high quality applications/systems with web services"
                "*Solid experience of Python, Java and exposure to HTML, JavaScript and CSS"
                "*Good knowledge of Python tools and frameworks, including Flask, Django, and MongoDB and Rabbit MQ."
                "*Competent at assigning APIs, and working with RESTful APIs"
                "*Experienced with distributed systems"
                "*Solid exposure to Linux, Git, Postgres (or similar)"
                "*Experience of working on greenfield projects is desirable"
                "*A demonstrable ability to cope under pressure and deliver to deadlines ",
    city="Kocaeli",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()
JobPost(
    title="Senior Android Developer 1",
    description="The Senior Android Developer will have significant experience of native Android app development processes in a commercial environment, as well as general development practices such as version control and agile development methodologies like TDD, BDD, CI."
                "Areas of work include:"
                "Mobile app development on Androi"
                "Supporting & realising UI & UX design"
                "Developing & administering micro services",
    city="London",
    country="United Kingdom",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()
JobPost(
    title="Senior Android Developer 2",
    description="The Senior Android Developer will have significant experience of native Android app development processes in a commercial environment, as well as general development practices such as version control and agile development methodologies like TDD, BDD, CI."
                "Areas of work include:"
                "Mobile app development on Androi"
                "Supporting & realising UI & UX design"
                "Developing & administering micro services",
    city="Istanbul",
    country="Turkey",
    user=muatik.user,
    longitude=1, latitude=1,
    employer="Ford"
    ).save()