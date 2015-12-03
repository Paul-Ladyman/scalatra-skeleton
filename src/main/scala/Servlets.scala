package bbc.cps.scalatraskeleton

import bbc.cps.scalatraskeleton.api.{BaseApi, IndexApi}

object Servlets {
  def apply(): Map[BaseApi, String] = {
    Map(
      new IndexApi -> "/*"
    )
  }
}
