#!/usr/bin/env python
# coding: utf-8

# Copyright 2017 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import dialogflow
import uuid

def explicit():
    from google.cloud import storage
    # Explicitly use service account credentials by specifying the private key file.
    storage_client = storage.Client.from_service_account_json('/Users/zoey/Desktop/dialogflow/seungwoojeong-ff342a8a00e6.json')
    
    # Make an authenticated API request
    buckets = list(storage_client.list_buckets())
    print(buckets)

# get 'string_value' in JSON object which describes entities
def get_string_value(response):
    from google.protobuf.json_format import MessageToDict
    # convery query_result to dict type
    query_result = MessageToDict(response.query_result)
    
    # get list type entities from query_result
    list_entities = query_result['parameters'].values()
    intent_entity = ''

    for entity in list_entities:
        # if entity is not empty then print entity
        if bool(entity.strip()):
            # encode unicode type entity to ascii type
            encode_entity = entity.encode('ascii','replace')
            # replace white space or questionmark with underscore
            intent_entity = encode_entity.replace(' ','_').replace('?','_')

    return intent_entity

# detect intent by text based on trained phrases in ATM-Agent
def detect_intent_texts(project_id, session_id, texts, language_code):
    '''
        Returns the result of detect intent with texts as inputs.
        Using the same `session_id` between requests allows continuation
        of the conversation.
    '''
    file = open('output.txt','w')
    
    import dialogflow_v2 as dialogflow
    session_client = dialogflow.SessionsClient()
    
    session = session_client.session_path(project_id, session_id)
    print('Session path: {}\n'.format(session))
    
    # print intent in each sentence with entities which is argument of function
    for text in texts:
        text_input = dialogflow.types.TextInput(text=text, language_code=language_code)
        query_input = dialogflow.types.QueryInput(text=text_input)
        response = session_client.detect_intent(session=session, query_input=query_input)
        detected_intent = response.query_result.intent.display_name
        
        file.write('=' * 20 + '\n')
        file.write('Query text: {}'.format(response.query_result.query_text))
        file.write('Detected intent: {} (confidence: {})\n'.format(detected_intent, response.query_result.intent_detection_confidence))
        intent_entity = get_string_value(response)
        file.write(detected_intent + '(' + intent_entity + ')\n')
        print('=' * 20)
        print('Query text: {}'.format(response.query_result.query_text))
        print('Detected intent: {} (confidence: {})\n'.format(detected_intent, response.query_result.intent_detection_confidence))
        print( detected_intent + '(' + intent_entity + ')')
    
    return detected_intent, intent_entity

if __name__ == '__main__':
    explicit()
    file = open('input.txt','r')
    query_text = file.readlines()
    detect_intent_texts('seungwoojeong-5b90a',str(uuid.uuid4()),query_text,'en-US')
