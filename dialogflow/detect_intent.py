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

def choose_service_account_file():
    '''
        This function help to choose to explictly point to your service account file in code.
        '''
    from google.cloud import storage
    # Explicitly use service account credentials by specifying the private key file.
    storage_client = storage.Client.from_service_account_json('/Users/zoey/Desktop/dialogflow/seungwoojeong-ff342a8a00e6.json')
    # Make an authenticated API request
    buckets = list(storage_client.list_buckets())
    print(buckets)

def get_enitity_names(response):
    '''
        This function return entity names of each text from string_value of parameters in query result
        
        input - response : <class 'google.cloud.dialogflow_v2.types.DetectIntentResponse'>
        output - entity_names : list
        '''
    from google.protobuf.json_format import MessageToDict
    # convert query_result to dict type
    query_result = MessageToDict(response.query_result)
    # get list type entities from query_result
    entities = query_result['parameters'].values()
    entity_names = []
    
    for entity in list_entities:
        # Check if the entity is empty
        if bool(entity.strip()):
            # Encode unicode type entity to ascii type
            encoded_entity_name = entity.encode('ascii','replace')
            # Replace white space or questionmark with underscore
            entity_names.append(encoded_entity_name.replace(' ','_').replace('?','_'))

    return entity_names


def detect_intent_texts(project_id, session_id, texts, language_code):
    '''
        This function detects intent with texts based on trained phrases
        in ATM-Agent and return the result of detect intent with as inputs.
        
        input - project_id : str, session_id : str, texts : list, language_code : str
        output - intent_name : str / intent name of each text in input.txt file
        
        '''
    
    # Open output.txt file
    file = open('output.txt','w')
    import dialogflow_v2 as dialogflow
    #session_client : <class 'dialogflow_v2.SessionsClient'>
    session_client = dialogflow.SessionsClient()
    # session : unicode
    session = session_client.session_path(project_id, session_id)
    
    # Print and write Query text and UML-text-format
    for text in texts:
        # text_input : <class 'google.cloud.dialogflow_v2.types.TextInput'>
        # Represents the natural language text to be processed.
        text_input = dialogflow.types.TextInput(text=text, language_code=language_code)
        # query_input : <class 'google.cloud.dialogflow_v2.types.QueryInput'>
        query_input = dialogflow.types.QueryInput(text=text_input)
        # response : <class 'google.cloud.dialogflow_v2.types.DetectIntentResponse'>
        # Contains response_id and query result which is selected results of the conversational query
        response = session_client.detect_intent(session=session, query_input=query_input)
        intent_name = response.query_result.intent.display_name
        participant_order_of_uml_text_format = get_participant_order(intent_name)
        print('=' * 40)
        print('Query text: {}'.format(response.query_result.query_text))
        print('output : {}'.format(participant_order_of_uml_text_format + intent_name))
        file.write(participant_order_of_uml_text_format + intent_name + '\n')
    
    return intent_name


def get_participant_order(intent_name):
    '''
        This function returns participant order(ATM->ATM) of UML text format
        with intent name as inputs.
        
        input - intent_name : str / intent name of each text in input.txt file
        output - participant_order : str / participant order of UML text format
        
        '''
    
    # Assign the specific actions to each situation
    # where one participant gives a message to another participant
    uml_text_format = {
        'User->ATM:': ['insert', 'enter', 'select'],
        'ATM->User:' : ['request', 'prompt'],
        'ATM->ATM:': ['validate']
    }
    
    # Find a participant order from intent name
    # when the verb of intent name matches with an action in uml_text_format
    for participant_order, actions in uml_text_format.items():
        for action in actions:
            if action in intent_name:
                return participant_order

if __name__ == '__main__':
    choose_service_account_file()
    file = open('input.txt','r')
    query_text = file.readlines()
    detect_intent_texts('seungwoojeong-5b90a',str(uuid.uuid4()),query_text,'en-US')


