import datetime
import os
import sqlite3
import traceback
from logger import Logger
import constants

def validate_db_logs(db_path):
    """Verifica os logs inseridos no banco de dados."""
    print("\n--- Validando Logs no Banco de Dados ---")
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM Logs")
    rows = cursor.fetchall()
    for row in rows:
        print(f"ID: {row[0]}, App: {row[1]}, Versão: {row[2]}, Criado por: {row[3]}, Nível: {row[7]}, Erro: {row[8]}")
    conn.close()

def validate_file_logs():
    """Verifica a existência e conteúdo dos arquivos de log locais."""
    print("\n--- Validando Arquivos de Log Locais ---")
    log_files = [f for f in os.listdir('.') if f.startswith('LOG_') and f.endswith('.txt')]
    if not log_files:
        print("Nenhum arquivo de log encontrado!")
        return
    
    for log_file in log_files:
        print(f"\nConteúdo de {log_file}:")
        with open(log_file, 'r') as f:
            print(f.read())

def test_success_flow():
    print("\n--- Testando Fluxo de Sucesso ---")
    logger = Logger(constants.DB_PATH)
    start_time = datetime.datetime.now()
    # Simula processamento
    end_time = datetime.datetime.now()
    logger.log_success("Teste Sucesso", start_time, end_time)

def test_error_flow():
    print("\n--- Testando Fluxo de Erro ---")
    logger = Logger(constants.DB_PATH)
    start_time = datetime.datetime.now()
    try:
        # Simula erro
        raise ValueError("Simulação de erro para validação de logs")
    except Exception as e:
        end_time = datetime.datetime.now()
        logger.log_error("Teste Erro", start_time, end_time, e)

if __name__ == "__main__":
    # Limpa logs antigos para o teste
    if os.path.exists(constants.DB_PATH):
        os.remove(constants.DB_PATH)
    for f in os.listdir('.'):
        if f.startswith('LOG_') and f.endswith('.txt'):
            os.remove(f)

    test_success_flow()
    test_error_flow()
    
    validate_db_logs(constants.DB_PATH)
    validate_file_logs()
