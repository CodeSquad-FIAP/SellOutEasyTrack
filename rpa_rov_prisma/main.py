import datetime
from api import ApiService
import constants
from logger import Logger

def main():
    logger = Logger(constants.DB_PATH)
    execution_start = datetime.datetime.now()
    created_by = "Manus RPA" # Pode ser parametrizado se necessário

    try:
        logger.log_to_file("Iniciando execução da automação ROV/PRISMA", "INFO")
        api_service = ApiService(constants.API_BASE_URL)
        
        # Simulação de login
        response_json = api_service.post(constants.API_GET_TOKEN_ENDPOINT, body={
          "userName": "admin",
          "password": "admin"
        })
        
        if response_json and 'token' in response_json:
            token = response_json['token']
            api_service.headers["Authorization"] = f"Bearer {token}"
            
            ocorrencias = api_service.get(constants.API_GET_OCORRENCIAS_ENDPOINT)
            if ocorrencias is not None:
                total_count = len(ocorrencias)
                counter = 1
                logger.log_to_file(f'Total de ocorrências para atualizar: {total_count}', "INFO")
                
                for ocorrencia in ocorrencias:
                    response = api_service.post(constants.API_UPDATE_OCORRENCIA_ENDPOINT, body=ocorrencia)
                    logger.log_to_file(f'Executando {counter}/{total_count} : {response}', "INFO")
                    counter += 1
            else:
                logger.log_to_file("Nenhuma ocorrência encontrada para atualizar.", "INFO")
        else:
            raise Exception("Falha ao obter token de autenticação.")

        execution_end = datetime.datetime.now()
        logger.log_success(created_by, execution_start, execution_end)

    except Exception as err:
        execution_end = datetime.datetime.now()
        logger.log_error(created_by, execution_start, execution_end, err)
        print(f"Erro inesperado: {err}")

if __name__ == '__main__':
    main()
