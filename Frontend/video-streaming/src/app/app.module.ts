import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS,HttpClientModule } from '@angular/common/http';
import { NgxFileDropModule } from 'ngx-file-drop';
import { UploadVideoComponent } from './upload-video/upload-video.component';
import {MatButtonModule} from "@angular/material/button";
import {HeaderComponent} from './header/header.component'

@NgModule({

  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    UploadVideoComponent,
    AppComponent,
    FormsModule,
    HttpClientModule,
    NgxFileDropModule,
    MatButtonModule,
    HeaderComponent
  ]
})
export class AppModule {}
