import {InjectableRxStompConfig} from '@stomp/ng2-stompjs'
import {environment} from '../environments/environment'

/**
 * Stomp configuration.
 */
export const StompConfig: InjectableRxStompConfig = {
  brokerURL: `${environment.apiBaseUrl.replace('http', 'ws')}/ws/websocket`
}
