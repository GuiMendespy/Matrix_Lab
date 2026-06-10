import requests
import json

def testar_agente(pergunta):
    url = "http://127.0.0.1:8000/chat"
    payload = {"message": pergunta}
    headers = {"Content-Type": "application/json"}
    print(f"\n--- [CHAT] Enviando Pergunta: {pergunta} ---")
    try:
        response = requests.post(url, json=payload, headers=headers)
        if response.status_code == 200:
            print("\n--- Resposta do Tutor ---")
            print(response.json().get("response"))
        else:
            print(f"\nErro {response.status_code}: {response.text}")
    except Exception as e:
        print(f"\nErro: {e}")

def testar_gerador(tema):
    url = "http://127.0.0.1:8000/generate"
    payload = {"message": tema}
    headers = {"Content-Type": "application/json"}
    print(f"\n--- [GERADOR] Criando questão sobre: {tema} ---")
    try:
        response = requests.post(url, json=payload, headers=headers)
        if response.status_code == 200:
            print("\n--- Questão Gerada com Sucesso ---")
            print(response.json().get("response"))
            print("\nO arquivo foi salvo em: rag/question_bank/")
        else:
            print(f"\nErro {response.status_code}: {response.text}")
    except Exception as e:
        print(f"\nErro: {e}")

if __name__ == "__main__":
    # COMENTE/DESCOMENTE O QUE QUISER USAR:
    
    # Para conversar com o Tutor:
    # testar_agente("O que é uma matriz identidade?")
    
    # Para GERAR uma questão e SALVAR no banco:
    testar_gerador("Matriz inversa")
