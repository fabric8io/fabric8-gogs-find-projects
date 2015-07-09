/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.devops.projects.finder.gogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.repo.git.GitRepoClient;
import io.fabric8.repo.git.RepositoryDTO;
import io.fabric8.utils.Strings;
import io.fabric8.utils.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 */
public class Main {
    private static final transient Logger LOG = LoggerFactory.getLogger(Main.class);

    private String address;
    private String username;
    private String password;

    public static void main(String[] args) {
        if (args.length > 0) {
            String arg0 = args[0];
            if (arg0.startsWith("-h") || arg0.startsWith("--h") || arg0.startsWith("?")) {
                System.out.println("Queries gogs for the available repositories");
                System.out.println("");
                System.out.println("Configure via the following environment variables:");
                System.out.println("");
                System.out.println("  GOGS_SERVICE_HOST:     the host name or IP address of the gogs HTTP REST API");
                System.out.println("  GOGS_SERVICE_PORT:     the port of the gogs HTTP REST API (omit for port 80)");
                System.out.println("  JENKINS_GOGS_USER:     user name to login to gogs");
                System.out.println("  JENKINS_GOGS_PASSWORD: password to login to gogs");
                System.out.println("");
                return;
            }
        }
        Main main = new Main();
        main.run();
    }

    public void run() {
        try {
            String host = Systems.getEnvVarOrSystemProperty("GOGS_SERVICE_HOST", "gogs.vagrant.f8");
            String port = Systems.getEnvVarOrSystemProperty("GOGS_SERVICE_PORT", "");
            address = "http://" + host + (Strings.isNotBlank(port) ? ":" + port : "");
            username = Systems.getEnvVarOrSystemProperty("JENKINS_GOGS_USER", "gogsadmin");
            password = Systems.getEnvVarOrSystemProperty("JENKINS_GOGS_PASSWORD", "RedHat$1");

            LOG.info("Logging into gogs at address: " + address + " with user " + username);

            GitRepoClient client = new GitRepoClient(address, username, password);
            List<RepositoryDTO> repos = client.listRepositories();
            System.out.println(JsonHelper.toJson(repos));
        } catch (JsonProcessingException e) {
            System.err.println("Failed to find repos: "+ e);
            e.printStackTrace(System.err);
        }
    }
}
