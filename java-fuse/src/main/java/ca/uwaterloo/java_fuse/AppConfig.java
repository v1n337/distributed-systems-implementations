package ca.uwaterloo.java_fuse;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
public class AppConfig
{
    private String serverAddress;
    private int serverPort;
    private String clientMountPoint;
}
