import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  private ownersUrl: string;
  private metaUrl: string;

  constructor(private http: HttpClient) { 
    this.ownersUrl = 'http://localhost:8080/users/names';
    this.metaUrl = 'http://localhost:8080/users/';
  }

  getOwnersList(): Observable<string[]> {
    return this.http.get<string[]>(this.ownersUrl);
  }

  getReposList(owner: string): Observable<any> {
    let reposUrl = this.metaUrl + owner + '/repositories'
    let params = new HttpParams()
      .set('owner', owner);
    return this.http.get<any>(reposUrl, {params});
  }

  getBranchesList(owner: string, repository: string): Observable<any> {
    let branchesUrl = this.metaUrl + owner + '/repositories/' + repository + '/branches';
    let params = new HttpParams()
      .set('owner', owner)
      .set('repository', repository);
    return this.http.get<any>(branchesUrl, {params});
  }

  getSnapshotsList(owner: string, repository: string, branch:string): Observable<any> {
    let snapshotsUrl = this.metaUrl + owner + '/repositories/' + repository + '/branches/' + branch + '/snapshots';
    let params = new HttpParams()
      .set('owner', owner)
      .set('repository', repository)
      .set('branch', branch);
    return this.http.get<any>(snapshotsUrl, {params});
  }
}
