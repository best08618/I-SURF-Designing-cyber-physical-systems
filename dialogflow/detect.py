import dialogflow
import uuid

def detect_intent_texts(project_id, session_id, texts, language_code):
    """Returns the result of detect intent with texts as inputs.
        Using the same `session_id` between requests allows continuation
        of the conversation."""
    file = open('output.txt','w')
    
    import dialogflow_v2 as dialogflow
    session_client = dialogflow.SessionsClient()
    
    session = session_client.session_path(project_id, session_id)
    print('Session path: {}\n'.format(session))
    
    for text in texts:
        text_input = dialogflow.types.TextInput(text=text, language_code=language_code)
        query_input = dialogflow.types.QueryInput(text=text_input)
        response = session_client.detect_intent(session=session, query_input=query_input)
        
        file.write('Query text: {}'.format(response.query_result.query_text))
        file.write('Detected intent: {} (confidence: {})\n'.format(response.query_result.intent.display_name, response.query_result.intent_detection_confidence))
        
        print('=' * 20)
        print('Query text: {}'.format(response.query_result.query_text))
        print('Detected intent: {} (confidence: {})\n'.format(response.query_result.intent.display_name, response.query_result.intent_detection_confidence))
        print('Fulfillment text: {}\n'.format(response.query_result.fulfillment_text))


if __name__ == '__main__':
    file = open('input.txt','r')
    query_text = file.readlines()
    #query_text = ["The system prints the receipt.","The  system ejects  the  ATM  card."]
    detect_intent_texts('seungwoojeong-5b90a',str(uuid.uuid4()),query_text,'en-US')


