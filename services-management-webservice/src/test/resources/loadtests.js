import {sleep, check, group} from "k6";
import http from "k6/http";

import jsonpath from "https://jslib.k6.io/jsonpath/1.0.2/index.js";

export const options = {
    stages: [
        {duration: "1m", target: 15},
        {duration: "2m", target: 15},
        {duration: "1m", target: 0},
    ],
    thresholds: {
        http_req_failed: ["rate<0.05"],
    },
    ext: {
        loadimpact: {
            distribution: {
                "amazon:us:ashburn": {loadZone: "amazon:us:ashburn", percent: 100},
            },
        },
    },
};

// Uncomment to launch tests locally
// export let IAM_URL = `https://iam-ci.crm.epic.infra/auth/realms/epic-ci/protocol/openid-connect/token`;
// export let SERVICE_URL= `http://api-ci.crm.epic.infra/tecrep/svc`;

export let IAM_URL = __ENV.ENV_IAM_URL;
export let SERVICE_URL = __ENV.ENV_SERVICE_URL;
export let USERNAME = __ENV.ENV_USERNAME;
export let PASSWORD = __ENV.ENV_PASSWORD;
export let CLIENT_SECRET = __ENV.ENV_CLIENT_SECRET;
export let CLIENT_ID = __ENV.ENV_CLIENT_ID;

export function setup() {
    let response;
    let requestBody = {};

    requestBody['grant_type'] = 'client_credentials';
    requestBody['username'] = USERNAME;
    requestBody['password'] = PASSWORD;
    requestBody['client_secret'] = CLIENT_SECRET;
    requestBody['client_id'] = CLIENT_ID;
    // 2. setup code
    group("page_1 - " + IAM_URL, function () {
        response = http.post(IAM_URL, requestBody,
            {
                headers: {
                    'Accept-Encoding': 'gzip,deflate',
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            }
        );
        check(response, {
            "status equals 200": response => response.status.toString() === "200",
        });
        if (response != null) {
            vars["kcAccessToken"] = jsonpath.query(response.json(), "$.access_token")[0];
        }
    });
    return vars["kcAccessToken"];
}

export let vars = {};

export default function main(data) {
    let response;

    vars["kcAccessToken2"] = data;


    group("page_2 - " + SERVICE_URL + "/private/auth/services/4", function () {
        response = http.get(SERVICE_URL + "/private/auth/services/4",
            {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${vars["kcAccessToken2"]}`
                },
            }
        );
        check(response, {
            "status equals 200": response => response.status.toString() === "200",
        });

    });
    // Automatically added sleep
    sleep(1);
}
