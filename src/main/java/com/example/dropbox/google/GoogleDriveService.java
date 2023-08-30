package com.example.dropbox.google;

import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;

@Service
public class GoogleDriveService {
   // private final Drive driveService;
    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
    private String clientSecret;

    public File createFolder(String folderName) {
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

      //  try {
       //     File folder = driveService.files().create(folderMetadata)
          //          .setFields("id")
         //           .execute();
        //   return folder;
     //   } catch (IOException e) {
       //     e.printStackTrace();
    //    }

        return null;
    }


    public void addFolderPermission(String folderId, String email, String writer) {
    }
}


