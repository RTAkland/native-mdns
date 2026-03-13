#include "win32_join_multicast.h"

#include <stdint.h>
#include <winsock2.h>
#include <ws2tcpip.h>

int join_multicast_group(SOCKET sock, const char *group) {
    struct ip_mreq mreq;
    mreq.imr_multiaddr.s_addr = inet_addr(group);
    mreq.imr_interface.s_addr = INADDR_ANY;

    if (setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *) &mreq, sizeof(mreq)) == SOCKET_ERROR) {
        return 1;
    }
    return 0;
}

int socket_sendto(SOCKET fd, const uint8_t *data, int len, const char *host, unsigned short port) {
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = inet_addr(host);

    const int sent = sendto(fd, (const char *) data, len, 0, (struct sockaddr *) &addr, sizeof(addr));
    if (sent == SOCKET_ERROR) {
        return 1;
    }
    return 0;
}

int leave_multicast_group(SOCKET sock, const char *group) {
    struct ip_mreq mreq;
    mreq.imr_multiaddr.s_addr = inet_addr(group);
    mreq.imr_interface.s_addr = INADDR_ANY;

    if (setsockopt(sock, IPPROTO_IP, IP_DROP_MEMBERSHIP, (char *) &mreq, sizeof(mreq)) == SOCKET_ERROR) {
        return 1;
    }
    return 0;
}