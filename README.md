# Core Service

## Overview
Core Service is a Kotlin Multiplatform library designed for device and network communication based on Remote Procedure Call (RPC). It supports both Android and Windows Desktop as primary platforms, with limited functionality on Linux and macOS. The core architecture focuses on the server-side implementation, while an optional standalone application allows running both server and client on the same host.

## Features
- **Multiplatform Support:** Android and Windows Desktop are the main targets, with Linux and macOS available but not as thoroughly tested.
- **RPC-Based Communication:** The library provides a structured RPC framework for communication between client and server.
- **Modular Design:** Core Service is divided into multiple modules to facilitate flexibility and maintainability.
- **Dependency Injection Friendly:** The client module is designed to integrate easily into dependency injection frameworks.

## Modules

### 1. `api`
Contains contracts for the RPC interface and necessary entity classes for communication between the server and client.

### 2. `client`
Required for any application that wants to access Core Service functionalities. It provides wrapper classes for RPC calls, designed to integrate with dependency injectors.

### 3. `exampleApp`
A testbed application used to validate and showcase Core Service functionalities.

### 4. `server`
The main entry point for implementing all RPC interfaces. It includes the server configuration, such as defining the IP and port for handling calls.

### 5. `server-api`
Defines interfaces for extending server-side functionalities into sub-modules.

### 6. `server-network`
Handles all network-related operations for Core Service. It abstracts Android and desktop network implementations, allowing the server module to delegate network-related tasks to this module.

## RPC Routes

The Core Service server currently defines three main RPC routes:

```kotlin
routing {
    rpc("/device") {
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<DeviceStatus> { ctx ->
            koin.get { parametersOf(ctx) }
        }
    }

    rpc("/config") {
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<ConfigurationModifier> { ctx ->
            koin.get { parametersOf(ctx) }
        }
    }

    rpc("/comms") {
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<Communication> { ctx ->
            koin.get<Communication> { parametersOf(ctx) }
        }
    }
}
```

## Getting Started

### Prerequisites
- Kotlin Multiplatform project setup
- Gradle (Kotlin DSL preferred)
- Koin Dependency Injection framework (optional but recommended)

### Installation
Include the Core Service dependencies in your project:
```kotlin
// Example Gradle dependencies
implementation("com.example.rpc:api:1.0.0")
implementation("com.example.rpc:client:1.0.0")
implementation("com.example.rpc:server:1.0.0")
```

### Usage
#### Setting up the Server
```kotlin
   viewModelScope.launch(Dispatchers.IO) {
    get<BasicService> { parametersOf(true, "127.0.0.1", serviceScope) }
}
```

#### Connecting with the Client
```kotlin
@Single([CommunicationFacade::class])
class CommunicationFacadeImpl : CommunicationFacade {

    override suspend fun setupNetworkCommunication(networkCommunication: NetworkCommunication): Flow<InboundNetworkMessage> {
        val client = HttpClient(CIO) { installKrpc() }.rpc {
            url {
                host = "127.0.0.1"
                port = SERVER_PORT
                encodedPath = "comms"
            }

            rpcConfig {
                serialization {
                    json()
                }
            }
        }

        return streamScoped {
            client.withService<Communication>().setupNetworkCommunication(
                networkCommunication
            )
        }
    }

    override suspend fun sendNetworkMessage(
        communicationId: Uuid,
        outboundMessage: OutboundMessage
    ): MessageSendResult {
        val client = HttpClient(CIO) { installKrpc() }.rpc {
            url {
                host = "127.0.0.1"
                port = SERVER_PORT
                encodedPath = "comms"
            }

            rpcConfig {
                serialization {
                    json()
                }
            }
        }

        return streamScoped {
            client.withService<Communication>().sendNetworkMessage(
                communicationId,
                outboundMessage
            )
        }
    }
}


@Factory
class BindToPortUseCase(private val communicationFacade: CommunicationFacade) {

    suspend fun invoke(interfaceName: String, networkUuid: Uuid) {
        communicationFacade.setupNetworkCommunication(
            NetworkCommunication.TcpUdpConnection(
                interfaceName,
                NetworkConnection.Udp.BoundPort(
                    1581
                ),
                networkUuid
            )
        )
    }
}

```

## Contribution
Contributions are welcome! Feel free to submit issues and pull requests.

## License
Core Service is licensed under the MIT License.

