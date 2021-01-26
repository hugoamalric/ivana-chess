import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http'
import {environment} from '../environments/environment'
import {map} from 'rxjs/operators'
import {Observable} from 'rxjs'

/**
 * Ivana Chess service.
 */
@Injectable({
  providedIn: 'root'
})
export abstract class IvanaChessService {
  /**
   * Initialize service.
   * @param http HTTP client.
   * @protected
   */
  protected constructor(
    protected http: HttpClient
  ) {}

  /**
   * Execute POST request.
   * @param uri URI.
   * @param body Request body.
   * @return Response body.
   * @protected
   */
  protected post<T>(uri: string, body: any): Observable<T> {
    return this.http.post(`${environment.apiBaseUrl}/${uri}`, body)
      .pipe(map(json => this.jsonSnakeCaseToCamelCase(json) as T))
  }

  /**
   * Convert JSON keys from snake case to camel case.
   * @param obj JSON.
   * @return JSON with camel case keys.
   * @private
   */
  private jsonSnakeCaseToCamelCase(obj: any): any {
    if (Array.isArray(obj)) {
      return obj.map(item => this.jsonSnakeCaseToCamelCase(item))
    } else if (Object(obj) === obj) {
      return Object.keys(obj)
        .map(key => {
          const convertedObj = {}
          // @ts-ignore
          convertedObj[snakeCaseToCamelCase(key)] = jsonSnakeCaseToCamelCase(obj[key])
          return convertedObj
        })
        .reduce((a, b) => {
          return {...a, ...b}
        }, {})
    } else {
      return obj
    }
  }

  /**
   * Convert string to snake case to camel case.
   * @param str String.
   * @return Camel case string.
   * @private
   */
  private snakeCaseToCamelCase(str: string): string {
    return str.replace(/([-_][a-z])/ig, (str) => {
      return str.toUpperCase()
        .replace('-', '')
        .replace('_', '');
    });
  }
}
