/**
 * All RabbitMQ listener and its business logic should placed with in this package. All CS server events are tracked and
 * update the status of resources in APP DB, update usage of resource in APP DB, sync APP DB while action directly
 * handled at CS server, CS server Alert by using corresponding listener.
 */
package ck.panda.rabbitmq.util;
