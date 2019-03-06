// See https://github.com/dialogflow/dialogflow-fulfillment-nodejs
// for Dialogflow fulfillment library docs, samples, and to report issues
'use strict';
 
const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
const {Card, Suggestion} = require('dialogflow-fulfillment');
 
process.env.DEBUG = 'dialogflow:debug'; // enables lib debugging statements
var named_user = "";
var today_counter = "";
var yesterday_counter = "";
var weekly_counter = '';
var fav_alternative = "";

 
exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });
  console.log('Dialogflow Request headers: ' + JSON.stringify(request.headers));
  console.log('Dialogflow Request body: ' + JSON.stringify(request.body));
 
  function welcome(agent) {
    agent.add(`Welcome to my agent!`);
  }
 
  function fallback(agent) {
    agent.add(`I didn't understand`);
    agent.add(`I'm sorry, can you try again?`);
  }
  
  function daily_counter(agent){
    var counter = 1;
    counter = (agent.parameters.number === null) ? 1 : agent.parameters.number; 
    var cig_counter = counter.toString();
    agent.add(`here are the parameters ${counter}`);
  }
  
  function store_name(agent){
  named_user = agent.parameters.any;
  agent.add(`Hello. It's nice to meet you ${named_user}. Ask me what I can do?`);  
  //ask_if_smokes(agent);  
  }
  
  function ask_if_smokes(agent){
  agent.add(`Do you smoke ${named_user}`);
  }
  
  function favourite_alternative(agent){
   fav_alternative = agent.parameters.alternative;
   var fav_alt_string = fav_alternative.toString().toLowerCase();
   var link_to_serve = 'https://www.health24.com/Medical/Stop-smoking/News/The-5-most-common-smoking-alternatives-20150611';

   switch(fav_alt_string) {
    case 'vapes':
      link_to_serve = 'https://www.centeronaddiction.org/e-cigarettes/recreational-vaping/what-vaping';
    break;
    case 'herbal cigarettes':
      link_to_serve = 'https://en.wikipedia.org/wiki/Herbal_cigarette';
    break;
    case 'patche':
      link_to_serve = 'https://www.nicodermcq.com/support-hub/all-about-the-nicotine-patch.html';
    break;
    default:
    
  }
  agent.add(`Follow the link of your favorite alternative ${fav_alternative}` );
  agent.add(`${link_to_serve}`);
  }



  // Uncomment and edit to make your own intent handler
  // uncomment `intentMap.set('your intent name here', yourFunctionHandler);`
  // below to get this function to be run when a Dialogflow intent is matched
  // function yourFunctionHandler(agent) {
  //   agent.add(`This message is from Dialogflow's Cloud Functions for Firebase editor!`);
  //   agent.add(new Card({
  //       title: `Title: this is a card title`,
  //       imageUrl: 'https://developers.google.com/actions/images/badges/XPM_BADGING_GoogleAssistant_VER.png',
  //       text: `This is the body text of a card.  You can even use line\n  breaks and emoji! 💁`,
  //       buttonText: 'This is a button',
  //       buttonUrl: 'https://assistant.google.com/'
  //     })
  //   );
  //   agent.add(new Suggestion(`Quick Reply`));
  //   agent.add(new Suggestion(`Suggestion`));
  //   agent.setContext({ name: 'weather', lifespan: 2, parameters: { city: 'Rome' }});
  // }

  // // Uncomment and edit to make your own Google Assistant intent handler
  // // uncomment `intentMap.set('your intent name here', googleAssistantHandler);`
  // // below to get this function to be run when a Dialogflow intent is matched
  // function googleAssistantHandler(agent) {
  //   let conv = agent.conv(); // Get Actions on Google library conv instance
  //   conv.ask('Hello from the Actions on Google client library!') // Use Actions on Google library
  //   agent.add(conv); // Add Actions on Google library responses to your agent's response
  // }
  // // See https://github.com/dialogflow/dialogflow-fulfillment-nodejs/tree/master/samples/actions-on-google
  // // for a complete Dialogflow fulfillment library Actions on Google client library v2 integration sample

  // Run the proper function handler based on the matched Dialogflow intent name
  let intentMap = new Map();
  intentMap.set('Default Welcome Intent', welcome);
  intentMap.set('Default Fallback Intent', fallback);
  intentMap.set('storing_name_and_greeting_back', store_name);
  intentMap.set('smoke_confirmation_add_counter', daily_counter);
  intentMap.set('How_do_you_work? - yes - custom', favourite_alternative);
  //intentMap.set('Do_you_smoke', ask_if_smokes);
  //intentMap.set('test_counter', daily_counter);
  // intentMap.set('your intent name here', yourFunctionHandler);
  // intentMap.set('your intent name here', googleAssistantHandler);
  agent.handleRequest(intentMap);
});


