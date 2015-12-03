package service

import bbc.cps.collaboration.Config.QueueAgent._
import bbc.cps.collaboration.service.{InactiveEditsPurgeService, InactiveEditsPurgeQueueClient}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, MustMatchers}
import org.mockito.Matchers._

class InactiveEditsPurgeQueueServiceSpec extends FlatSpec with BeforeAndAfter with MustMatchers with MockitoSugar {
  val sqsClientMock = mock[AmazonSQSClient]
  val inactiveEditsPurgeServiceMock = mock[InactiveEditsPurgeService]

  val inactiveEditsPurgeQueueService = new InactiveEditsPurgeQueueClient(sqsClientMock, inactiveEditsPurgeServiceMock)

  val messageId: String = "6cde8ea6-d4b3-58d9-9430-c36ad0276e18"
  val body = "purge-inactive-edits"
  val receiveMessageRequest = new ReceiveMessageRequest(sqsUrl).withWaitTimeSeconds(20)
  val message = new Message().withReceiptHandle(messageId).withBody(body)

  before {
    reset(sqsClientMock, inactiveEditsPurgeServiceMock)
  }

  "processSqsMessages()" should "purge inactive edits and delete message if a message is found on the queue" in {
    when(sqsClientMock.receiveMessage(receiveMessageRequest)).thenReturn(new ReceiveMessageResult().withMessages(message))

    inactiveEditsPurgeQueueService.purgeInactiveEdits()

    verify(inactiveEditsPurgeServiceMock).updateEditStatus()
    verify(sqsClientMock).deleteMessage(sqsUrl, message.getReceiptHandle)
  }

  it should "purge inactive edits and delete each message when multiple messages are found on the queue" in {
    val secondMessage = new Message().withReceiptHandle("b92cbf79-9826-413f-8a53-c4007a3744b9").withBody(body)
    when(sqsClientMock.receiveMessage(receiveMessageRequest))
      .thenReturn(new ReceiveMessageResult().withMessages(message, secondMessage))

    inactiveEditsPurgeQueueService.purgeInactiveEdits()
    verify(inactiveEditsPurgeServiceMock, times(2)).updateEditStatus()
    verify(sqsClientMock).deleteMessage(sqsUrl, message.getReceiptHandle)
    verify(sqsClientMock).deleteMessage(sqsUrl, secondMessage.getReceiptHandle)
  }

  it should "do nothing if no message is found on the queue" in {
    when(sqsClientMock.receiveMessage(receiveMessageRequest)).thenReturn(new ReceiveMessageResult)

    inactiveEditsPurgeQueueService.purgeInactiveEdits()

    verifyZeroInteractions(inactiveEditsPurgeServiceMock)
    verify(sqsClientMock, never()).deleteMessage(anyString, anyString)
  }

  "receiveMessages()" should "return a List if a messages are found" in {
    when(sqsClientMock.receiveMessage(receiveMessageRequest)).thenReturn(new ReceiveMessageResult().withMessages(message))

    val receivedMessage = inactiveEditsPurgeQueueService.receiveMessages()

    receivedMessage must be (List(message))
  }

  it should "return an empty List if no message is found" in {
    when(sqsClientMock.receiveMessage(receiveMessageRequest)).thenReturn(new ReceiveMessageResult())

    val receivedMessage = inactiveEditsPurgeQueueService.receiveMessages()

    receivedMessage must be (List.empty)
  }

  "deleteMessage()" should "delete messages off the SQS queue" in {
    inactiveEditsPurgeQueueService.deleteMessage(message.getReceiptHandle)

    verify(sqsClientMock).deleteMessage(sqsUrl, message.getReceiptHandle)
  }
}
