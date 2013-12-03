Using with Maven
================

MondoCommand is best if used with Maven


Add this repository:

.. code-block:: xml

    <repository>
        <id>crast-repo</id>
        <url>http://maven.crast.us</url>
    </repository>

And this dependency:

.. code-block:: xml

    <dependency>
        <groupId>us.crast</groupId>
        <artifactId>MondoCommand</artifactId>
        <version>0.3</version>
        <type>jar</type>
        <scope>compile</scope>
    </dependency>

And also remember to shade the package so that there's no conflicts with other plugins.

.. code-block:: xml

  <relocation>
      <pattern>mondocommand</pattern>
      <shadedPattern>me.something.your-project-name.mondocommand</shadedPattern>
  </relocation>
