/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.palisade.example.hrdatagenerator.types;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.UserId;

import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

public class Manager {
    private UserId uid;
    private Manager[] managers;
    private String managerType;

    public static Manager[] generateMany(final Random random, final int chain) {
        Manager[] managers = new Manager[3];
        managers[0] = Manager.generateRecursive(random, chain, "Line Manager");
        managers[1] = Manager.generateRecursive(random, chain, "Task Manager");
        managers[2] = Manager.generateRecursive(random, chain, "Career Manager");
        return managers;
    }


    public static Manager generateRecursive(final Random random, final int chain, final String managerType) {
        Manager manager = Manager.generate(random, managerType);
        if (chain <= 1) {
            manager.setManager(null);
        } else {
            manager.setManager(Manager.generateMany(random, chain - 1));
        }
        return manager;
    }

    public static Manager generate(final Random random, final String managerType) {
        Manager manager = new Manager();
        manager.setUid(Employee.generateUID(random));
        manager.setManagerType(managerType);

        return manager;
    }

    @Generated
    public UserId getUid() {
        return uid;
    }

    @Generated
    public void setUid(final UserId uid) {
        this.uid = uid;
    }

    @Generated
    public Manager[] getManager() {
        if (null == managers) {
            return managers;
        } else {
            return managers.clone();
        }
    }

    @Generated
    public void setManager(final Manager[] managers) {
        if (null == managers) {
            this.managers = managers;
        } else {
            this.managers = managers.clone();
        }
    }

    @Generated
    public String getManagerType() {
        return managerType;
    }

    @Generated
    public void setManagerType(final String managerType) {
        this.managerType = managerType;
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", Manager.class.getSimpleName() + "[", "]")
                .add("uid=" + uid)
                .add("manager=" + Arrays.toString(managers))
                .add("managerType='" + managerType + "'")
                .toString();
    }
}
