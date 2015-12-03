package bbc.cps.scalatraskeleton.api

class IndexApi extends BaseApi {

  get("/") {
    contentType = "application/json"
    """{"message":"Welcome to your new application"}"""
  }

  get("/status") {
    contentType = "application/json"
    """{"status":"OK"}"""
  }

}
