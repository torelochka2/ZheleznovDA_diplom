from django.db import models

class ParsedDocument(models.Model):
    title = models.TextField(null=True)
    pages = models.TextField(null=True)
    pub_year = models.TextField(null=True)
    author = models.TextField(null=True)
    time_created = models.DateTimeField(auto_now_add=True)
    #todo добавить doi, ссылку откуда взят документ??

    def __str__(self):
        return self.title




