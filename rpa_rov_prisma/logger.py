import os
import datetime
import traceback
import sqlite3 # Usando sqlite3 para simular o banco de dados SQL Server no sandbox
from constants import APPLICATION_NAME, APPLICATION_VERSION

class Logger:
    def __init__(self, db_path="logs.db"):
        self.application_name = APPLICATION_NAME
        self.application_version = APPLICATION_VERSION
        self.db_path = db_path
        self._init_db()

    def _init_db(self):
        """Inicializa a tabela de logs se não existir."""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS Logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ApplicationName TEXT,
                ApplicationVersion TEXT,
                CreatedBy TEXT,
                CreatedAt DATETIME,
                ExecutionStart DATETIME,
                ExecutionEnd DATETIME,
                LogLevel TEXT,
                ExceptionMessage TEXT,
                ExceptionStackTrace TEXT
            )
        ''')
        conn.commit()
        conn.close()

    def log_to_file(self, message, level="INFO"):
        """Grava log em arquivo local LOG_AAAAMMDDHHmm.txt."""
        timestamp = datetime.datetime.now().strftime("%Y%m%d%H%M")
        filename = f"LOG_{timestamp}.txt"
        log_entry = f"[{datetime.datetime.now().isoformat()}] [{level}] {message}\n"
        with open(filename, "a") as f:
            f.write(log_entry)

    def log_to_db(self, created_by, start_time, end_time, level, message=None, stack_trace=None):
        """Insere log no banco de dados."""
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            query = '''
                INSERT INTO Logs (
                    ApplicationName, ApplicationVersion, CreatedBy, CreatedAt,
                    ExecutionStart, ExecutionEnd, LogLevel, ExceptionMessage, ExceptionStackTrace
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            '''
            cursor.execute(query, (
                self.application_name,
                self.application_version,
                created_by,
                datetime.datetime.now().isoformat(),
                start_time.isoformat() if start_time else None,
                end_time.isoformat() if end_time else None,
                level,
                message,
                stack_trace
            ))
            conn.commit()
            conn.close()
        except Exception as e:
            print(f"Erro ao gravar log no banco: {e}")

    def log_success(self, created_by, start_time, end_time):
        msg = "Execução finalizada com sucesso."
        self.log_to_file(msg, "SUCCESS")
        self.log_to_db(created_by, start_time, end_time, "SUCCESS")

    def log_error(self, created_by, start_time, end_time, exception):
        error_msg = str(exception)
        stack_trace = traceback.format_exc()
        self.log_to_file(f"Erro: {error_msg}", "ERROR")
        self.log_to_db(created_by, start_time, end_time, "ERROR", error_msg, stack_trace)
