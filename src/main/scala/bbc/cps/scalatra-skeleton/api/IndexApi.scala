package bbc.cps.collaboration.api

class IndexApi extends BaseApi {

  get("/") {
    contentType = "application/json"
    """{"message":"Welcome to BBC Vivo Collaboration Service"}"""
  }

  get("/status") {
    contentType = "application/json"
    """{"status":"OK"}"""
  }

}
