package io.jenkins.plugins.autonomiq.service.types;

public enum TransportMsgType {
    MSG_USED,
    MSG_CREATE_SESSION,                        // 1
    MSG_TEST_REQUEST,                          // 2
    MSG_TEST_RESPONSE_UNPROCESSED_INSTRUCTIONS, // 3
    MSG_TEST_RESPONSE_GENERATED_SCRIPT,         // 4
    MSG_TEST_RESPONSE_TEST_CASE_FINISHED,       // 5
    MSG_MESSAGE_ERROR_FROM_UI,                  // 6
    MSG_SCRIPT_GENERATION_STARTED,              // 7
    MSG_SCRIPT_GENERATION_FINISHED,             // 8
    MSG_SESSION_CLOSE,                          // 9
    MSG_NLP_UNIQUE_SUBJECT,                     // 10
    MSG_TEST_STOP,                              // 11
    MSG_ALERT_MANUAL,                           // 12
}
