# Run: guard -p -l 10

guard :shell do
    log = Logger.new(STDOUT)

    watch(/[A-Za-z0-9]+\.(class|java|groovy|js|html|ftl|vm|yml|xml|properties)$/) { |m|
        log.info("${r"#{m[0]}"} has been changed.")

        atime = File.atime(m[0])
        mtime =  File.mtime(m[0])

        File.utime(atime, mtime, m[0])
    }
end