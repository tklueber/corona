import {Component, OnInit} from '@angular/core';
import {DataService} from "./service/data.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'dashboard';

  land = undefined;
  sumCases = undefined;
  sumDeaths = undefined;
  firstCase = undefined;
  lastCase = undefined;

  constructor(private data: DataService) {
  }

  ngOnInit(): void {
    this.data.loadRegionData('DE').subscribe(
      data => {
        this.land = data.name;
        this.sumCases = data.sumCases;
        this.sumDeaths = data.sumDeaths;
      }
    )
  }
}
