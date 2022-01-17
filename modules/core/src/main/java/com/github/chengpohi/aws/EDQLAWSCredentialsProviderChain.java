package com.github.chengpohi.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class EDQLAWSCredentialsProviderChain extends AWSCredentialsProviderChain {
    public EDQLAWSCredentialsProviderChain(String awsAccessKeyId,
                                           String awsAccessKeySecret,
                                           String awsSessionToken) {
        super(new AWSCredentialsProvider() {
                  @Override
                  public AWSCredentials getCredentials() {
                      return awsSessionToken == null ?
                              new BasicAWSCredentials(awsAccessKeyId, awsAccessKeySecret) :
                              new BasicSessionCredentials(awsAccessKeyId, awsAccessKeySecret, awsSessionToken);
                  }

                  @Override
                  public void refresh() {
                  }
              }, new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                WebIdentityTokenCredentialsProvider.create(),
                new ProfileCredentialsProvider(),
                new EC2ContainerCredentialsProviderWrapper());
    }

    public EDQLAWSCredentialsProviderChain() {
        super(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                WebIdentityTokenCredentialsProvider.create(),
                new ProfileCredentialsProvider(),
                new EC2ContainerCredentialsProviderWrapper());
    }
}
