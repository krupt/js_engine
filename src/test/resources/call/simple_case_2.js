/**
 * Created by a.kovalev on 17.12.2017
 */
meta = { // Описание колла
  description: 'Этот колл проверяет существование клиента',
  invocationInfo: {
    inputs: [
      {
        name: 'CLIENT_ID',
        type: 'TEXT'
      }
    ],
    outputs: [
      {
        name: 'EXISTS',
        "type": "BOOLEAN"
      },
      {
        name: 'ERROR_MESSAGE',
        "type": "TEXT",
        required: false
      }
    ]
  },
  tests: [
    {
      name: 'test1',
      inputs: {
        'CLIENT_ID': 'TEST'
      },
      outputs: {
        'EXISTS': true,
        'ERROR_MESSAGE': null
      }
    }
  ]
};

const engine = require("call_engine");

callFunction = function () {
  const clientId = engine.input('CLIENT_ID');
  engine.output('EXISTS', clientId && clientId === 'TEST');
};
