import socket
from zeroconf import ServiceInfo, Zeroconf

def start_mdns():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        local_ip = s.getsockname()[0]
        s.close()

        desc = {'path': '/chat'}

        info = ServiceInfo(
            "_http._tcp.local.",
            "MatrixServer._http._tcp.local.",
            addresses=[socket.inet_aton(local_ip)],
            port=8000,
            properties=desc,
            server="matrix-server.local.",
        )

        zeroconf = Zeroconf()
        zeroconf.register_service(info)

        print(f"Agente Online em: http://{local_ip}:8000")
        print(f"Anunciando como: http://matrix-server.local:8000")

        return zeroconf

    except Exception as e:
        print(f"Erro ao iniciar mDNS: {e}")
        return None