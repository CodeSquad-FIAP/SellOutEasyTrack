import requests
import json

class ApiService:

    def __init__(self, base_url):
        self.base_url = base_url
        self.headers = {
            'Content-type': 'application/json',
            'Accept': 'application/json'
        }

    def get(self, endpoint, parameters=None):
        try:
            response = requests.get(
                url=f"{self.base_url}/{endpoint}",
                params=parameters,
                headers=self.headers,
                verify=False)

            if response.status_code == 200:
                return response.json()
            else:
                print(f"{response.status_code} error with your request")
                return None
        except Exception as err:
            print(f"Unexpected {err=}, {type(err)=}")
            raise

    def post(self, endpoint, body=None):
        try:
            payload = json.dumps(body)
            response = requests.post(
                url=f"{self.base_url}/{endpoint}",
                data=payload,
                headers=self.headers,
                verify=False)

            if response.status_code == requests.codes.ok:
                return response.json()
            else:
                print("error with your request")
                print(f"Status: {response.status_code}")
                print(f"Message: {response.text}")
                return None
        except Exception as err:
            print(f"Unexpected {err=}, {type(err)=}")
            raise
