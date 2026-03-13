#ifndef WIN32_JOIN_MULTICAST_H
#define WIN32_JOIN_MULTICAST_H

#include <stdint.h>
#include <winsock2.h>

#ifdef __cplusplus
extern "C" {

#endif

int join_multicast_group(SOCKET sock, const char *group);

int socket_sendto(SOCKET fd, const uint8_t *data, int len, const char *host, unsigned short port);

int leave_multicast_group(SOCKET sock, const char *group);

#ifdef __cplusplus
}
#endif

#endif // WIN32_JOIN_MULTICAST_H
