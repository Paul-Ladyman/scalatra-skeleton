package bbc.cps.collaboration

import bbc.cps.collaboration.Config.{Mongo, NotificationAgent}
import bbc.cps.collaboration.api.{BaseApi, IndexApi, PostEditApi}
import bbc.cps.collaboration.persistence.PostEditDao
import bbc.cps.collaboration.service.{NotificationService, PostEditService}

object Servlets {
  def apply(): Map[BaseApi, String] = {
    Map(
      new IndexApi -> "/*",
      new PostEditApi(new PostEditService(new PostEditDao(Mongo.postEditsCollection), new NotificationService(NotificationAgent()))) -> "/post-edits/*"
    )
  }
}
