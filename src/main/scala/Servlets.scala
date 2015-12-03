package bbc.cps.scalatraskeleton

import bbc.cps.scalatraskeleton.Config.{Mongo, NotificationAgent}
import bbc.cps.scalatraskeleton.api.{BaseApi, IndexApi, PostEditApi}
import bbc.cps.scalatraskeleton.persistence.PostEditDao
import bbc.cps.scalatraskeleton.service.{NotificationService, PostEditService}

object Servlets {
  def apply(): Map[BaseApi, String] = {
    Map(
      new IndexApi -> "/*"
    )
  }
}
