/**
 * Created by a.kovalev on 12.03.2018
 */
meta = {
  description: 'Возвращает текущую дату',
  invocationInfo: {
    outputs: [
      {
        name: 'CURRENT_DATE',
        "type": "DATE"
      }
    ]
  },
  tests: [
    {
      name: 'test1',
      outputs: {
        'CURRENT_DATE': '2018-03-12'
      }
    }
  ]
};

const engine = require("call_engine");

callFunction = function () {
  engine.output('CURRENT_DATE', new Date().toISOString().substr(0, 10));
};
