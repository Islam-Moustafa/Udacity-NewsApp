/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.islam.udacity_news;

// class News contain information related to single book
public class News {

    // title of the News
    private String title;

    // sectionName of the News, such Technology, Science ...
    private String sectionName;

    // publish date of the News
    private String webPublicationDate;

    // website URL of the News, to take more details
    private String url;

    /**
     * Constructs a new News object.
     *
     * @param title is the title of the News
     * @param sectionName is the section name of the News
     * @param webPublicationDate is the date of published News
     * @param url is the website URL to read more details about the News
     */
    public News(String title, String sectionName , String webPublicationDate, String url){
        this.title = title;
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.url = url;
    }

    // return title of the News
    public String getTitle(){return title;}

    // return sectionName of the News
    public String getSectionName(){return sectionName;}

    // return webPublicationDate of the News
    public String getWebPublicationDate(){return webPublicationDate;}

    //return URL of the News
    public String getUrl(){return url;}
}
