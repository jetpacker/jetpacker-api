guard :shell do
    watch(/[A-Za-z0-9]+\.(class|java|groovy|js|html|ftl|vm|yml|xml|properties)$/) { |m|
        log = Logger.new(STDOUT)
        log.info("#{m[0]} has been changed.")

        atime = File.atime(m[0])
        mtime =  File.mtime(m[0])

        File.utime(atime, mtime, m[0])
    }
end